FROM node:22-alpine

ENV NODE_ENV=production \
    PORT=8080 \
    HOST=0.0.0.0

WORKDIR /app

COPY --chown=node:node package.json server.js ./
USER node

EXPOSE 8080

CMD ["node", "server.js"]
