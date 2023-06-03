import uuid
from locust import HttpUser, task, between


class FireballClientUser(HttpUser):
    host = "http://localhost:58080"
    wait_time = between(1, 5)

    @task
    def upload_file(self):
        # remote_file_path = str(uuid.uuid4()) + ".dat"
        remote_file_path = "remote-1000.dat"
        body = {
                "localFile": "local-1000.dat",
                "remoteIp": "127.0.0.1",
                "remotePort": 12345,
                "remoteFile": remote_file_path
            }
        self.client.post("/upload", json=body)