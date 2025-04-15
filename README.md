# 🛳️ Ship Proxy (Client) & Offshore Proxy (Server)

This project consists of:
- **Ship Proxy (Client)**: Acts as a proxy configured in your browser or terminal (e.g., `curl`).
- **Offshore Proxy (Server)**: Forwards actual HTTP/S requests and returns responses back to the client.
- Requests are handled **sequentially**, one by one, ensuring request order is preserved.

---

## 📦 Features

✅ Acts as an HTTP proxy that you can configure in Chrome or use with `curl`  
✅ Sequential request processing (one at a time)  
✅ Compatible with HTTP and HTTPS  
✅ Packaged in Docker containers  
✅ Cross-platform usage (Linux, macOS, Windows)

---

## 🐳 Running with Docker Compose

### 🔧 Prerequisites
- Docker and Docker Compose installed on your system

---

### 🚀 Getting Started

```bash
# Clone the project
git clone https://github.com/niyascode007/the_ship.git
cd the_ship

# Build and start the containers
docker-compose up --build
