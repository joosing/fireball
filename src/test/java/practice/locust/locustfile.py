import time
from locust import HttpUser, task, between


class QuickstartUser(HttpUser):
    host = "http://localhost:8080"
    wait_time = between(1, 5)

    @task
    def download_file(self):
        self.client.post("/files/local/local-100.dat",
                         json={"ip": "127.0.0.1", "port": "12345", "filePath": "remote-100.dat"})

    @task
    def upload_file(self):
        self.client.post("/files/remote/127.0.0.1/12345/remote-100.dat",
                         json={"filePath": "local-100.dat"})

    # @task(3)
    # def view_items(self):
    #     for item_id in range(10):
    #         self.client.get(f"/item?id={item_id}", name="/item")
    #         time.sleep(1)
    #
    # def on_start(self):
    #     self.client.post("/login", json={"username": "foo", "password": "bar"})
