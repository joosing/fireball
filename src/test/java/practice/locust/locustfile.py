import uuid
from locust import HttpUser, task, between


class QuickstartUser(HttpUser):
    host = "http://localhost:8080"
    wait_time = between(1, 5)

    # @task
    # def download_file(self):
    #     local_file_path = str(uuid.uuid4()) + ".dat"
    #     body = {
    #         "local": {
    #             "filePath": local_file_path
    #         },
    #         "remote": {
    #             "ip": "127.0.0.1",
    #             "port": 12345,
    #             "filePath": "remote.dat"
    #         }
    #     }
    #     self.client.post("/download", json=body)

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

    # @task(3)
    # def view_items(self):
    #     for item_id in range(10):
    #         self.client.get(f"/item?id={item_id}", name="/item")
    #         time.sleep(1)
    #
    # def on_start(self):
    #     self.client.post("/login", json={"username": "foo", "password": "bar"})
