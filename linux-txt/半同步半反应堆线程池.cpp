#ifndef THREADPOOL_H
#define THREADPOOL_H
#include <list>
#include <cstdio>
#include <exception>
#include <pthread.h>

template<typename T>
class threadpool
{
    public:
        threadpool(int thread_number=8, int max_requests=10000);
        ~threadpool();
        bool append(T* request);
    
    private:
        static void* worker(void* arg);
        void run();

    private:
        int m_thread_number;
        int m_max_requests;
        pthread_t *m_threads;
        std::list<T*> m_workqueue;
        locker m_queue_lock;
        sem m_queue_stat;
        bool m_stop;
};

template<typename T>
threadpool<T>::threadpool(int thread_number, int max_requests):
                            m_thread_number(thread_number),
                            m_max_requests(max_requests), 
                            m_stop(false), 
                            m_threads(NULL)
{
    if ((thread_number<=0)||(max_requests<=0))
    {
        throw std::exception();
    }

    m_threads = new pthread_t[m_thread_number];
    if (!m_threads)
    {
        throw std::exception();
    }

    for (int i=0; i<thread_number; i++)
    {
        printf("create the %dth thread\r\n", i);
        if (pthread_create(m_threads+i, NULL, worker, this)!=0)
        {
            delete[] m_threads;
            throw std::exception();
        }

        if (pthread_detach(m_threads[i]))
        {
            delete[] m_threads;
            throw std::exception();
        }
    }

}

template<typename T>
threadpool<T>::~threadpool()
{
    delete[] m_threads;
    m_stop = true;
}

template<typename T>
bool threadpool<T>::append(T* request)
{
    m_queue_lock.lock();
    if (m_workqueue.size()>m_max_requests)
    {
        m_queue_lock.unlock();
        return false;
    }
    m_workqueue.push_back(request);
    m_queue_lock.unlock();
    m_queue_stat.post();

    return true;
}

template<typename T>
void* threadpool<T>::worker(void* arg)
{
    threadpool *pool = (threadpool*)arg;
    pool->run();
    return pool;
}

template<typename T>
void threadpool<T>::run()
{
    while(!m_stop)
    {
        m_queue_stat.wait();
        m_queue_lock.lock();
        if (m_workqueue.empty())
        {
            m_queue_lock.unlock();
            continue;
        }

        T* request = m_workqueue.front();
        m_workqueue.pop_front();
        m_queue_lock.unlock();

        if (!request)
        {
            continue;
        }
        request->process();
    }
}

int setnonblocking(int fd)
{
    int old_option=fcntl(fd, F_GETFL);
    int new_option=old_option|O_NONBLOCK;
    fcntl(fd, F_SETFL, new_option);
    return old_option;
}

void addfd(int epollfd, int fd, bool one_shot)
{
    epoll_event event;
    event.data.fd = fd;
    event.events = EPOLLIN|EPOLLET|EPOLLRDHUP;
    if (one_shot)
    {
        event.events|=EPOLLONSHOT;
    }
    epoll_ctl(epollfd, EPOLL_CTL_ADD, fd, &event);
    setnonblocking(fd);
}

void addsig(int sig, void(handler)(int), bool restart=true)
{
    struct sigaction sa;
    memset(&sa, '\0', sizeof(sa));
    sa.sa_handler = handler;
    if (restart)
    {
        sa.sa_handler |= SA_RESTART;
    }
    sigfillset(&sa.sa_mask);
    assert(sigaction(sig,&sa,NULL)!=-1);
}

int main(int argc, char *argv[])
{
    if (argc <= 2)
    {
        printf("usage:");
        return 1;
    }

    const char *ip = argv[1];
    int port = atoi(argv[2]);
    addsig(SIGPIPE, SIG_IGN);//信号忽略
    threadpool<http_conn> *pool = NULL;
    try
    {
        pool = new threadpool<http_conn>;
    }
    catch(const std::exception& e)
    {
        std::cerr << e.what() << '\n';
    }
    
    http_conn *users = new http_conn[MAX_FD];
    assert(users);

    int user_count=0;
    int listenfd = socket(PF_INET,SOCK_STREAM, 0);
    assert(listenfd>=0)
    struct linger tmp = {1,0};
    setsockopt(listenfd,SOL_SOCKET,SO_LINGER,&tmp,sizeof(tmp));
    int ret = 0;
    struct sockaddr_in address;
    bzero(&address, sizeof(address));
    address.sin_family = AF_INET;
    inet_pton(AF_INET, ip, &address.sin_addr);
    address.sin_port = htons(port);
    ret = bind(listenfd, (struct sockeaddr*)&address, sizeof(address));
    assert(ret>0);
    ret = listen(listenfd,5);
    assert(ret>0);
    epoll_event events[MAX_EVENT_NUMBER];
    int epollfd = epoll_create(5);
    assert(epollfd!=-1);
    addfd(epollfd, listenfd, false);
    http_conn::m_epollfd = epollfd;
    while (true)
    {
        int number = epoll_wait(epollfd, events, MAX_EVENT_NUMBER, -1);
        if ((number<0)&&(errno!=EINTR))
        {
            printf("epoll failure\n");
            break;
        }

        for (int i=0; i<number; i++)
        {
            int sockfd = events[i].data.fd;
            if (sockfd == listenfd)
            {
                struct sockaddr_in client_address;
                socklen_t client_addrlength = sizeof(client_address);
                int connfd=accept(listenfd,(struct sockaddr*)&client_address,
                                    &client_addrlength);
                if (connfd<0)
                {
                    printf("error");
                    continue;
                }
                if (http_conn::m_user_count>=MAX_FD)
                {
                    
                }
                users[connfd].init(connfd, client_address);
            }
            else if (events[i].events&(EPOLLRDHUP|EPOLLHUP|EPOLLERR))
            {
                users[sockfd].close_conn();
            }
            else if(events[i].events&EPOLLIN)
            {
                if (users[sockfd].read())
                {
                    pool->append(users+sockfd);
                }
                else
                {
                    users[sockfd].close_conn();
                }
            }
            else if (events[i].events&EPOLLOUT)
            {
                if (!users[sockfd].write())
                {
                    users[sockfd].close_conn();
                }
            }
            else
            {}
        
        }
    }
    close(epollfd);
    close(listenfd);
    delete[] users;
    delete pool;
    return 0;
}



#endif