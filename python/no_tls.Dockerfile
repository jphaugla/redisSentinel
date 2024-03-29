FROM python:3.11
RUN mkdir /app
WORKDIR /app
COPY pyproject.toml poetry.lock README.md ./
COPY app ./app
ENV PYTHONPATH=${PYTHONPATH}:${PWD}
RUN pip3 install poetry
RUN poetry config virtualenvs.create false
RUN poetry install --no-dev
CMD ["poetry", "run", "python", "app/get_set_redis.py"]
