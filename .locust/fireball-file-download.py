import uuid
from locust import HttpUser, task, between


class QuickstartUser(HttpUser):
    host = "http://localhost:8080"
    wait_time = between(1, 5)

    @task
    def download_file(self):
        local_file_path = str(uuid.uuid4()) + ".dat"
        body = {
            "local": {
                "filePath": local_file_path
            },
            "remote": {
                "ip": "127.0.0.1",
                "port": 12345,
                "filePath": "remote-1000.dat"
            }
        }
        self.client.post("/download", json=body)
