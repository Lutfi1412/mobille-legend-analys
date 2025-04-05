package api

import (
	"api-ml/handler"
	"net/http"

	"github.com/rs/cors"
)

func Handler(w http.ResponseWriter, r *http.Request) {
	// Buat server G4Vercel
	mux := http.NewServeMux()
	mux.HandleFunc("/op", handler.OpHandler)
	mux.HandleFunc("/meta", handler.MetaHandler)
	mux.HandleFunc("/under", handler.UnderHandler)

	corsHandler := cors.New(cors.Options{
		AllowedOrigins:   []string{"*"},
		AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowedHeaders:   []string{"Content-Type", "Authorization"},
		AllowCredentials: true,
	})
	corsWrappedHandler := corsHandler.Handler(mux)
	corsWrappedHandler.ServeHTTP(w, r)
}
