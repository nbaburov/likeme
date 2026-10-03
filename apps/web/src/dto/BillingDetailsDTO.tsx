export interface BillingDetailsResponse {
	firstName: string;
	lastName: string;
	country: string;
	streetAddress: string;
	city: string;
	state: string;
	zipCode: string;
}

export interface CreateBillingDetailsRequest {
	firstName: string;
	lastName: string;
	country: string;
	streetAddress: string;
	city: string;
	state: string;
	zipCode: string;
}

export interface UpdateBillingDetailsRequest {
	firstName?: string;
	lastName?: string;
	country?: string;
	streetAddress?: string;
	city?: string;
	state?: string;
	zipCode?: string;
}
