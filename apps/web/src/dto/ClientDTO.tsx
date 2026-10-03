import {
	BillingDetailsResponse,
	CreateBillingDetailsRequest,
	UpdateBillingDetailsRequest,
} from "./BillingDetailsDTO";

export interface ClientResponse {
	id: number;
	username: string;
	email: string;
	profilePhotoPath: string;
	instagramHandle: string;
	isInstagramConnected: boolean;
	instagramAccessToken: string;
	billingDetails: BillingDetailsResponse;
	isActive: boolean;
	createdOn: Date;
	updatedOn: Date;
	lastLoginOn: Date;
}

export interface CreateClientRequest {
	username: string;
	email: string;
	profilePhotoPath: string;
	password: string;
	instagramHandle: string;
	billingDetails: CreateBillingDetailsRequest;
}

export interface UpdateClientRequest {
	username?: string;
	email?: string;
	password?: string;
	profilePhotoPath?: string;
	instagramHandle?: string;
	isInstagramConnected?: boolean;
	instagramAccessToken?: string;
	billingDetails?: UpdateBillingDetailsRequest;
	isActive?: boolean;
}
