import uuid
from locust import HttpUser, task, between


class QuickstartUser(HttpUser):
    host = "http://localhost:8080"
    wait_time = between(1, 5)

    @task
    def upload_file(self):
        remote_file_path = str(uuid.uuid4()) + ".dat"
        body = {
            "local": {
                "filePath": "local.dat"
            },
            "remote": {
                "ip": "127.0.0.1",
                "port": 12345,
                "filePath": remote_file_path
            }
        }
        self.client.post("/upload", json=body)