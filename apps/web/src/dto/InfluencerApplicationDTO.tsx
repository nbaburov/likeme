import { BillingDetailsResponse, CreateBillingDetailsRequest, UpdateBillingDetailsRequest } from "./BillingDetailsDTO";

export interface InfluencerApplicationResponse {
	id: number;
	username: string;
	email: string;
	phoneNumber: string;
	about: string;
	instagramHandle: string;
	profilePhotoPath: string;
	coverPhotoPath: string;
	isApproved: boolean;
	billingDetails: BillingDetailsResponse;
	createdOn: Date;
	updatedOn: Date;
}


export interface CreateInfluencerApplicationRequest {
	username: string;
	email: string;
	phoneNumber: string;
	about: string;
	instagramHandle: string;
	profilePhotoPath: string;
	coverPhotoPath: string;
	billingDetails: CreateBillingDetailsRequest;
}


export interface UpdateInfluencerApplicationRequest {
	username?: string;
	email?: string;
	phoneNumber?: string;
	about?: string;
	instagramHandle?: string;
	profilePhotoPath?: string;
	coverPhotoPath?: string;
	isApproved?: boolean;
	billingDetails?: UpdateBillingDetailsRequest;
}
