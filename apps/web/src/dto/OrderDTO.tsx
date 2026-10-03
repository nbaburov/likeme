import { InvoiceResponse } from "./InvoiceDTO";
import { OfferResponse } from "./OfferDTO";

export enum OrderStatus {
	PENDING = "PENDING",
	COMPLETE = "COMPLETE",
}

export interface OrderDetailsRequest {
	postId?: string;
	comment?: string;
}

export interface OrderDetailsResponse {
	postId: string;
	comment: string;
}

export interface CreateOrderRequest {
	offerId: number;
	details: OrderDetailsRequest;
}

export interface UpdateOrderRequest {
	details: OrderDetailsRequest;
}

export interface OrderResponse {
	id: number;
	offer: OfferResponse;
	details: OrderDetailsResponse;
	invoice: InvoiceResponse;
	orderedById: number;
	updatedById: number;
	status: OrderStatus;
	createdOn: Date;
	updatedOn: Date;
}
