all: client server

client: client.o func_camlog.o func_getIP.o func_general.o func_video.o func_param.o
	gcc client.o func_camlog.o func_getIP.o func_general.o func_video.o  func_param.o -o client

server: server.o func_camlog.o func_getIP.o func_general.o func_video.o func_param.o
	gcc server.o func_camlog.o func_getIP.o func_general.o func_video.o func_param.o -o server

client.o: client.c
	gcc -c -g client.c

server.o: server.c
	gcc -c -g server.c

func_camlog.o: func_camlog.c
	gcc -c -g func_camlog.c

func_getIP.o:func_getIP.c
	gcc -c -g func_getIP.c

func_general.o:func_general.c
	gcc -c -g func_general.c

func_video.o:func_video.c
	gcc -c -g func_video.c

func_param.o:func_param.c
	gcc -c -g func_param.c

clean:
	rm *.o client server -rf