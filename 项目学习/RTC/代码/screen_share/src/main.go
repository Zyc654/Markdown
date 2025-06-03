package main

import (
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
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

func startHttp(port string) {
	fmt.Printf("Start http port: %s\n", port)
	err := http.ListenAndServe(port, nil)
	if err != nil {
		fmt.Println(err)
	}
}

func startHttps(port, cert, key string) {
	fmt.Printf("Start https port: %s\n", port)
	err := http.ListenAndServeTLS(port, cert, key, nil)
	if err != nil {
		fmt.Println(err)
	}
}

/**
 * @brief 主函数，程序入口
 */
func main() {
	// 初始化HTTP服务器
	err := initServer(":8080")
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
