package main

import (
	"api-ml/handler"
	"log"
	"net/http"
)

func main() {
	http.HandleFunc("/op", handler.OpHandler)
	http.HandleFunc("/meta", handler.MetaHandler)
	http.HandleFunc("/under", handler.UnderHandler)

	log.Println("Server berjalan di :8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
