import uuid
from locust import HttpUser, task, between


class MultipartFileUser(HttpUser):
    host = "http://localhost:8080"
    wait_time = between(1, 5)

    @task
    def upload_file(self):
        file_path = "./files/multipart-1000.dat"

        with open(file_path, "rb") as file:
            self.client.post("/test/multipart/upload", files={"file": file})
