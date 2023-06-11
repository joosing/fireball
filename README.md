![image](https://github.com/joosing/fireball/assets/34666301/d4665a3f-84de-4197-8007-4e7d21c663e3)

# 1. Overview

The Fireball project started with the following goals.

- It provides the fast large file transfer service with minimal memory usage.
- It provides programmatically controllable services via HTTP requests.
- It provides both file server and client functionality in one service.
- It provides a small and independent file transfer service.

Now you can build, install, run this service at your network and you can quickly and easily transfer large files with the following HTTP request.

```
Method: POST
URL: http://192.168.100.10:58080/api/upload
Content-Type: application/json

{
  "source": {
    "file": "source.dat"
  },
  "destination": {
    "ip": "192.168.100.11",
    "port": 50711,
    "file": "destination.dat"
  }
}
```

# 2. Motivation

Anyone can easily write code to exchange files over a network. However, depending on which tool you use and how well you optimize it, there is a big difference in processing performance.

### Many Methods, Difference Performance

I tested sending a 1GB file over a loopback network using several representative methods (including Fireball). After 50 iterations of the test, the average was measured, and a typical performance home laptop (Acer Swift 5 Notebook) was used for the test. The time for one measurement includes the time from reading the file, sending it to the network, saving the received file again, and until the response is complete. In the test results below, you can see that even though we're processing the same file under the same conditions, which method we use makes a big difference in performance.

![image](https://github.com/joosing/fireball/assets/34666301/4614ef06-ad73-4723-b625-f948c22b5cc2)

![image](https://github.com/joosing/fireball/assets/34666301/51477d39-fa13-4812-818a-352aa6c7a953)

# 3. Build

Fireball is a Spring Boot project and uses Maven as its build tool. You can now clone the project, navigate to the root path (where the mvnw file exists), and build the project using the maven wrapper (mvnw). The build result file exists in ./target/fireball-0.0.1.jar.

```
git clone https://github.com/joosing/fireball.git
cd fireball
chmod 744 mvnw
./mvnw clean package -DskipTests
```

# 4. Install

To use Fireball to exchange files between different network hosts, the Fireball service must be installed and running on both hosts.

- First, make sure you have the Java 17 version installed.

```
java --version 
```

- Copy the built JAR file to the desired path.

```
cp ./target/fireball-0.0.1.final.jar /app/fireball/fireball-0.0.1.final.jar
```

# 5. Run

Run the Fireball with the following settings

- file.client.root : Specify the path to the file root on the client (required)
- file.server.root : Specify the path to the file root on the server (required)
- server.port : Specify the API server port (Optional, uses port 58080 if not specified)
- file.server.port : Specify the file server port (Optional, uses port 50711 if not specified)

```
java "-Dfile.server.root=/app/files" "-Dfile.client.root=/app/files" -jar fireball-0.0.1.jar
```

# 6. Usage

Fireball provides two REST APIs for file upload and download. Here is an example of a system configured using Fireball. In this example, the user service is installed on host A (192.168.10.10), and the Fireball service is installed on hosts B (192.168.100.10) and C (192.168.100.11). User sends a file exchange request to Host B. In this case, the Fireball service on Host B will be in the file client role and Fireball on Host C will be in the file server role. The following sections describe example requests that perform file uploads and downloads between Hosts B and C within this configuration.

![image](https://github.com/joosing/fireball/assets/34666301/fc36d798-fcd8-4b9f-b87c-34c7b06536c2)

### A. Upload

You can upload a file from Host B to Host C with a simple HTTP request like the following. The example uploads the source.dat file on host B to the /parent/destination.dat file on host C. Each file path is relative to the set root path. If the /parent directory does not exist, it will automatically create it and if the destination.dat file already exists, it will overwrite the existing file and create a new one.

```
Method: POST
URL: http://192.168.100.10:58080/api/upload
Content-Type: application/json

{
  "source": {
    "file": "source.dat"
  },
  "destination": {
    "ip": "192.168.100.11",
    "port": 50711,
    "file": "/parent/destination.dat"
  }
}
```

### B. Download

You can download files from host C to B with a simple HTTP request like the following. The example downloads the source.dat file on host C to the /parent/destination.dat file on host B. Each file path is relative to the set root path. If the /parent directory does not exist, it will automatically create it, and if the destination.dat file already exists, it will overwrite the existing file and create a new one.

```
Method: POST
URL: http://192.168.100.10:58080/api/download
Content-Type: application/json

{
  "source": {
    "ip": "192.168.100.11",
    "port": 50711,
    "file": "source.dat"
  },
  "destination": {
    "file": "/parent/destination.dat"
  }
}
```

### Response

Fireball uses custom HTTP response headers ("Error-No", "Error-Message") to share detailed error status.

```
Error-No: 1
Error-Message: The file does not exist.
```

| Error-No | Error-Message | HTTP Response Code |
| --- | --- | --- |
| 0 | OK. | 200 (OK) |
| 4000 | The file does not exist. | 400 (BAD_REQUEST) |
| 4001 | Item is not a file. | 400 (BAD_REQUEST) |
| 5000 | Internal System Error. | 500 (INTERNAL_SERVER_ERRO) |
| 5001 | No response from server. | 500 (INTERNAL_SERVER_ERRO) |

# 7. Design

Fireball sends a large file by splitting them into a series of small chunks (5 MB). On the receiving end, it receives one chunk from the network, buffers it in memory, and then writes it to the file at a time. To do this, We configure the following application message protocol:

- length: chunk message length (length from id to the end of the chunk contents)
- id : message id
- chunk type : indicate the position of the chunk in the file (start, middle, end)
- chunk contents : contents of chunk file

![image](https://github.com/joosing/fireball/assets/34666301/6c9ae5f5-6900-454a-872d-3baf018bf884)

# 8. Fireball

The concept of “fast” goes well with the concept of “fire”. I thought the concept of sending a "file" would also fit well with throwing a "ball". The reason why I chose basketball among various balls is not only because I like it, but also because it expresses the concept of the project well. A basketball is heavier than a baseball or other balls. So it's not easy to throw far away quickly. The Fireball project wants to transfer large files like this basketball as fast as blazing fast.

# 9. Plan

The Fireball project is prioritizing the following tasks:

- Build valuable unit test suites
- Channel encryption and user authentication
- Support batch file processing APIs

# 10. Contact

Please feel free to let me know if you have any problems using it, or if you have any suggestions for how to make it better. I'd love to know more about the problems or needs you have in the field, and to make Fireball better for you. And I'm available for full-time work anywhere in the world if you need similar types of work or collaboration. (I'm also available for remote work.)

- joosing711@gmail.com
