package handler

import (
	"api-ml/model"
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"sort"
)

var (
	apiURL     = "https://api.gms.moontontech.com/api/gms/source/2669606/2756568"
	authHeader = "hLal4Mf1ZPiSBWebWJbzP8kbwMg="
)

func fetchHeroes() ([]model.Hero, error) {
	payload := map[string]interface{}{
		"pageSize":  128,
		"pageIndex": 1,
		"filters": []map[string]interface{}{
			{"field": "bigrank", "operator": "eq", "value": "7"},
			{"field": "match_type", "operator": "eq", "value": 0},
		},
		"sorts": []map[string]interface{}{
			{"data": map[string]interface{}{"field": "main_hero_ban_rate", "order": "asc"}, "type": "sequence"},
			{"data": map[string]interface{}{"field": "main_heroid", "order": "desc"}, "type": "sequence"},
		},
		"fields": []string{
			"main_hero", "main_hero_appearance_rate", "main_hero_ban_rate",
			"main_hero_channel", "main_hero_win_rate", "main_heroid",
			"data.sub_hero.hero", "data.sub_hero.hero_channel", "data.sub_hero.increase_win_rate", "data.sub_hero.heroid",
		},
	}
	payloadBytes, _ := json.Marshal(payload)
	req, _ := http.NewRequest("POST", apiURL, bytes.NewBuffer(payloadBytes))
	req.Header.Set("Authorization", authHeader)
	req.Header.Set("Content-Type", "application/json;charset=UTF-8")

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	bodyBytes, _ := ioutil.ReadAll(resp.Body)

	if resp.StatusCode != 200 {
		return nil, fmt.Errorf("API error: %s", string(bodyBytes))
	}

	var result struct {
		Data struct {
			Records []struct {
				Data struct {
					MainHero struct {
						Data struct {
							Name string `json:"name"`
						} `json:"data"`
					} `json:"main_hero"`
					MainHeroID       int     `json:"main_heroid"`
					MainHeroWinRate  float64 `json:"main_hero_win_rate"`
					MainHeroPickRate float64 `json:"main_hero_appearance_rate"`
					MainHeroBanRate  float64 `json:"main_hero_ban_rate"`
				} `json:"data"`
			} `json:"records"`
		} `json:"data"`
	}

	json.Unmarshal(bodyBytes, &result)

	var heroes []model.Hero
	for _, record := range result.Data.Records {
		h := record.Data
		heroes = append(heroes, model.Hero{
			Name:     h.MainHero.Data.Name,
			ID:       h.MainHeroID,
			WinRate:  h.MainHeroWinRate * 100,  // ubah ke persen
			PickRate: h.MainHeroPickRate * 100, // ubah ke persen
			BanRate:  h.MainHeroBanRate * 100,  // ubah ke persen
		})
	}

	return heroes, nil
}

func OpHandler(w http.ResponseWriter, r *http.Request) {
	heroes, err := fetchHeroes()
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	var filtered []model.HeroResponse
	for _, h := range heroes {
		if h.BanRate > 20 && h.WinRate > 52 { // contoh filter OP
			filtered = append(filtered, model.HeroResponse{
				Name:     h.Name,
				ID:       h.ID,
				WinRate:  fmt.Sprintf("%.2f%%", h.WinRate),
				PickRate: fmt.Sprintf("%.2f%%", h.PickRate),
				BanRate:  fmt.Sprintf("%.2f%%", h.BanRate),
			})
		}
	}

	// Urutkan berdasarkan BanRate terbesar
	sort.SliceStable(filtered, func(i, j int) bool {
		return filtered[i].BanRate > filtered[j].BanRate
	})

	// Bungkus ke dalam response struct
	response := map[string]interface{}{
		"data": filtered,
	}

	json.NewEncoder(w).Encode(response)
}

func MetaHandler(w http.ResponseWriter, r *http.Request) {
	heroes, err := fetchHeroes()
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	var filtered []model.HeroResponse
	for _, h := range heroes {
		if h.BanRate > 1.5 && h.WinRate > 51 { // contoh filter OP
			filtered = append(filtered, model.HeroResponse{
				Name:     h.Name,
				ID:       h.ID,
				WinRate:  fmt.Sprintf("%.2f%%", h.WinRate),
				PickRate: fmt.Sprintf("%.2f%%", h.PickRate),
				BanRate:  fmt.Sprintf("%.2f%%", h.BanRate),
			})
		}
	}
	sort.SliceStable(filtered, func(i, j int) bool {
		return filtered[i].PickRate > filtered[j].PickRate
	})

	response := map[string]interface{}{
		"data": filtered,
	}
	json.NewEncoder(w).Encode(response)
}

func UnderHandler(w http.ResponseWriter, r *http.Request) {
	heroes, err := fetchHeroes()
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	var filtered []model.HeroResponse
	for _, h := range heroes {
		if h.BanRate < 0.75 && h.WinRate > 53 && h.PickRate < 0.75 { // contoh filter OP
			filtered = append(filtered, model.HeroResponse{
				Name:     h.Name,
				ID:       h.ID,
				WinRate:  fmt.Sprintf("%.2f%%", h.WinRate),
				PickRate: fmt.Sprintf("%.2f%%", h.PickRate),
				BanRate:  fmt.Sprintf("%.2f%%", h.BanRate),
			})
		}
	}
	sort.SliceStable(filtered, func(i, j int) bool {
		return filtered[i].WinRate > filtered[j].WinRate
	})
	response := map[string]interface{}{
		"data": filtered,
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}
