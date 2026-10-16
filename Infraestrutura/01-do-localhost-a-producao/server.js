const http = require('node:http');

const cursos = [
  { id: 1, nome: 'Java para iniciantes', cargaHoraria: 20 },
  { id: 2, nome: 'APIs com Node.js', cargaHoraria: 12 },
  { id: 3, nome: 'Docker do zero', cargaHoraria: 8 },
];

const port = Number(process.env.PORT || 8080);
const host = process.env.HOST || '0.0.0.0';

const server = http.createServer((request, response) => {
  if (request.url !== '/cursos') {
    response.writeHead(404, { 'Content-Type': 'application/json; charset=utf-8' });
    response.end(JSON.stringify({ erro: 'Rota não encontrada' }));
    return;
  }

  if (request.method !== 'GET') {
    response.writeHead(405, {
      Allow: 'GET',
      'Content-Type': 'application/json; charset=utf-8',
    });
    response.end(JSON.stringify({ erro: 'Método não permitido' }));
    return;
  }

  response.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' });
  response.end(JSON.stringify(cursos));
});

server.listen(port, host, () => {
  console.log(`API disponível em http://${host}:${port}`);
});
