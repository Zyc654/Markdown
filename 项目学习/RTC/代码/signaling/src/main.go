package main

import (
	"flag"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"signaling/src/framework"
)

// 全局变量，用于存储HTTP服务器实例
var server *http.Server

/**
 * @brief 初始化HTTP服务器
 * @param addr 服务器地址
 * @return 成功返回nil，失败返回错误
 */
func initServer(addr string) error {
	server = &http.Server{
		Addr: addr,
	}
	return nil
}

/**
 * @brief 启动HTTP服务器
 * @return 成功返回nil，失败返回错误
 */
func startServer() error {
	return server.ListenAndServe()
}

/**
 * @brief 停止HTTP服务器
 * @return 成功返回nil，失败返回错误
 */
func stopServer() error {
	return server.Close()
}

func main() {
	flag.Parse()

	err := framework.Init("./conf/framework.conf")
	if err != nil {
		panic(err)
	}

	// 静态资源处理 /static
	framework.RegisterStaticUrl()

	// 初始化HTTP服务器
	err = initServer(":8080")
	if err != nil {
		log.Fatalf("Failed to initialize server: %v", err)
	}

	// 启动HTTP服务器
	go func() {
		if err := startServer(); err != nil {
			log.Fatalf("Failed to start server: %v", err)
		}
	}()

	// 注册信号处理函数
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)

	// 等待信号
	<-sigChan

	// 停止HTTP服务器
	if err := stopServer(); err != nil {
		log.Fatalf("Failed to stop server: %v", err)
	}
}

func startHttp() {
	err := framework.StartHttp()
	if err != nil {
		panic(err)
	}
}

func startHttps() {
	err := framework.StartHttps()
	if err != nil {
		panic(err)
	}
}
