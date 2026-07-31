# 前端多阶段构建：Node 构建 + Nginx 托管
FROM node:20-alpine AS builder
WORKDIR /build
COPY frontend/package*.json ./
RUN npm config set registry https://registry.npmmirror.com && npm install
COPY frontend/ ./
RUN npm run build

FROM nginx:1.25-alpine
COPY --from=builder /build/dist /usr/share/nginx/html
COPY deploy/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
