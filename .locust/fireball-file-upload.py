import uuid
from locust import HttpUser, task, between


class FireballClientUser(HttpUser):
    host = "http://localhost:58080"
    wait_time = between(1, 5)

    @task
    def upload_file(self):
        # remote_file_path = str(uuid.uuid4()) + ".dat"
        remote_file = "remote-1000.dat"
        body = {
          "source": {
            "file": "local-1000.dat"
          },
          "destination": {
            "ip": "127.0.0.1",
            "port": 50711,
            "file": remote_file
          }
        }
        self.client.post("/api/upload", json=body)