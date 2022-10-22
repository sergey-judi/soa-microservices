FROM python:3.10-slim-bullseye
WORKDIR /workdir/server
COPY . /workdir/server
RUN pip install -r requirements.txt
EXPOSE 5000
ENTRYPOINT [ "python", "main.py"]
