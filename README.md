# Illinois Institute of Technology course CS553 Google App Engine project


This project aims to learn how to use the Google App Engine to implement a non-trivial distributed system.

Built a distributed storage system on top of Google App Engine, using Google Cloud Storage Client Library. The storage system is supporting storing arbitrary files using a unique key, think of a key/value store, where the key is a unique filename, and the value would be a file of arbitrary size. 
The Google Cloud Storage will be a persistent storage solution, but could have particularly high latency overheads for small files. 
One goal of the project is to improve the small file size performance by using memcache as a cache for small files (<=100KB). Since memcache is a distributed in-memory data structure, accessing small files from memory should be significantly faster than accessing such small files from persistent storage. Medium and large files should not be cached in memory, as in practice, large storage systems will generally have much more persistent storage capacity than volatile memory available.
