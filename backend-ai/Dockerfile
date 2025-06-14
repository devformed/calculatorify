FROM python:3.12-slim

WORKDIR /app

# Install curl for healthcheck and Poetry for dependency management
RUN apt-get update \
    && apt-get install -y curl \
    && pip install poetry \
    && rm -rf /var/lib/apt/lists/*

# Copy project metadata and install dependencies
COPY pyproject.toml poetry.lock ./
 RUN poetry config virtualenvs.create false \
    && poetry install --no-root

# Copy source code
COPY . .

EXPOSE 8000

# Start the FastAPI application
CMD ["uvicorn", "backend_ai.api:app", "--host", "0.0.0.0", "--port", "8000"]