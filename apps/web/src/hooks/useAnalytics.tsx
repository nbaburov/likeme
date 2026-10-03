/* eslint-disable @typescript-eslint/no-explicit-any */
import { useQuery } from "@tanstack/react-query";
import { axiosInstance } from "@/lib/axios";
import { OrderAnalyticsResponse } from "../dto/AnalyticsDTO";
import { ApiError } from "../dto/ErrorDTO";

const ANALYTICS_QUERY_KEY = "analytics";

export const useInfluencerOrderAnalytics = (influencerId: number) => {
	return useQuery<OrderAnalyticsResponse, ApiError>({
		queryKey: [ANALYTICS_QUERY_KEY, "orders", influencerId],
		enabled: influencerId > 0,
		queryFn: async () => {
			try {
				const { data } =
					await axiosInstance.get<OrderAnalyticsResponse>(
						`/analytics/orders/influencer/${influencerId}`
					);
				return data;
			} catch (error: any) {
				if (error.response?.data) {
					throw ApiError.fromDTO(error.response.data);
				}
				throw new ApiError(
					"FAILED_TO_FETCH_ANALYTICS",
					"Failed to fetch order analytics",
					error.message,
					new Date().toISOString()
				);
			}
		},
	});
};
