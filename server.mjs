import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const distCandidates = [
  path.join(__dirname, 'dist'),
  path.join(__dirname, 'web', 'dist'),
];

function resolveDistDir() {
  for (const candidate of distCandidates) {
    if (fs.existsSync(path.join(candidate, 'index.html'))) {
      return candidate;
    }
  }
  return distCandidates[0];
}

const DIST_DIR = resolveDistDir();
const PUBLIC_DIR = path.join(__dirname, 'public');

const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.htm': 'text/html; charset=utf-8',
  '.js': 'text/javascript; charset=utf-8',
  '.mjs': 'text/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.map': 'application/json; charset=utf-8',
  '.txt': 'text/plain; charset=utf-8',
  '.svg': 'image/svg+xml',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.gif': 'image/gif',
  '.webp': 'image/webp',
  '.ico': 'image/x-icon',
  '.woff': 'font/woff',
  '.woff2': 'font/woff2',
  '.ttf': 'font/ttf',
  '.otf': 'font/otf',
  '.mp3': 'audio/mpeg',
  '.wav': 'audio/wav',
  '.mp4': 'video/mp4',
  '.webm': 'video/webm',
};

function sendFile(res, filePath, isIndex = false, headOnly = false) {
  const ext = path.extname(filePath).toLowerCase();
  const contentType = MIME_TYPES[ext] || 'application/octet-stream';
  const cacheControl = isIndex
    ? 'no-cache, no-store, must-revalidate'
    : filePath.includes(`${path.sep}assets${path.sep}`)
      ? 'public, max-age=31536000, immutable'
      : 'public, max-age=3600';

  res.writeHead(200, {
    'Content-Type': contentType,
    'Cache-Control': cacheControl,
  });

  if (headOnly) {
    res.end();
    return;
  }

  const stream = fs.createReadStream(filePath);
  stream.on('error', () => {
    if (!res.headersSent) {
      res.writeHead(500, { 'Content-Type': 'text/plain; charset=utf-8' });
    }
    res.end('Internal Server Error');
  });
  stream.pipe(res);
}

function safeResolve(baseDir, reqPath) {
  const normalized = path.posix.normalize(reqPath).replace(/^(\.\.(\/|\\|$))+/, '');
  const trimmed = normalized.replace(/^\/+/, '');
  const fullPath = path.resolve(baseDir, trimmed);
  if (fullPath !== baseDir && !fullPath.startsWith(baseDir + path.sep)) {
    return null;
  }
  return fullPath;
}

const server = http.createServer((req, res) => {
  const method = req.method || 'GET';
  if (method !== 'GET' && method !== 'HEAD') {
    res.writeHead(405, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('Method Not Allowed');
    return;
  }

  let pathname = '/';
  try {
    const url = new URL(req.url || '/', `http://${req.headers.host || 'localhost'}`);
    pathname = decodeURIComponent(url.pathname);
  } catch {
    res.writeHead(400, { 'Content-Type': 'text/plain; charset=utf-8' });
    res.end('Bad Request');
    return;
  }

  if (pathname === '/health' || pathname === '/healthz') {
    res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
    res.end(JSON.stringify({ status: 'ok' }));
    return;
  }

  const headOnly = method === 'HEAD';

  const candidatePath = safeResolve(DIST_DIR, pathname === '/' ? 'index.html' : pathname);
  if (candidatePath && fs.existsSync(candidatePath) && fs.statSync(candidatePath).isFile()) {
    sendFile(res, candidatePath, path.basename(candidatePath) === 'index.html', headOnly);
    return;
  }

  const publicCandidate = safeResolve(PUBLIC_DIR, pathname);
  if (publicCandidate && fs.existsSync(publicCandidate) && fs.statSync(publicCandidate).isFile()) {
    sendFile(res, publicCandidate, false, headOnly);
    return;
  }

  const indexPath = path.join(DIST_DIR, 'index.html');
  if (!path.extname(pathname) && fs.existsSync(indexPath)) {
    sendFile(res, indexPath, true, headOnly);
    return;
  }

  res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
  res.end('Not Found');
});

const defaultPort = Number(process.env.DEFAULT_APP_PORT) || 3000;
const preferredPort = process.env.NGINX_PORT
  ? defaultPort
  : Number(process.env.PORT) || defaultPort;

let activePort = preferredPort;

server.on('error', (err) => {
  if (err && err.code === 'EADDRINUSE' && activePort !== defaultPort) {
    activePort = defaultPort;
    server.listen(activePort, '0.0.0.0');
    return;
  }
  console.error('Server failed to start:', err);
  process.exit(1);
});

server.listen(activePort, '0.0.0.0', () => {
  console.log(`MY HAPPY PLACE listening on http://0.0.0.0:${activePort} (serving ${DIST_DIR})`);
});

function shutdown() {
  server.close(() => process.exit(0));
  setTimeout(() => process.exit(0), 5000).unref();
}

process.on('SIGTERM', shutdown);
process.on('SIGINT', shutdown);
