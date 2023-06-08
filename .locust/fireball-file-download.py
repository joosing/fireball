import uuid
from locust import HttpUser, task, between


class FireballClientUser(HttpUser):
    host = "http://localhost:58080"
    wait_time = between(1, 5)

    @task
    def download_file(self):
        # local_file_path = str(uuid.uuid4()) + ".dat"
        source_file = "local-1000.dat"
        body = {
          "source": {
            "ip": "127.0.0.1",
            "port": 50711,
            "file": "remote-1000.dat"
          },
          "destination": {
            "file": source_file
          }
        }
        self.client.post("/api/download", json=body)
