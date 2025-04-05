package model

type Hero struct {
	Name     string  `json:"name"`
	ID       int     `json:"id"`
	WinRate  float64 `json:"win_rate"`
	PickRate float64 `json:"pick_rate"`
	BanRate  float64 `json:"ban_rate"`
}

type HeroResponse struct {
	Name     string `json:"name"`
	ID       int    `json:"id"`
	WinRate  string `json:"win_rate"`
	PickRate string `json:"pick_rate"`
	BanRate  string `json:"ban_rate"`
}
