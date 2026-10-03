export interface OrderAnalyticsResponse {
	influencerId: number;
	ordersByCountry: Record<string, number>;
}
