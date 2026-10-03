export enum OfferType {
	LIKE = "LIKE",
	COMMENT = "COMMENT",
	FOLLOW = "FOLLOW"
}

export interface CreateOfferRequest {
	title: string;
	description: string;

	coverPhotoPath: string;
	type: OfferType;
	createdById: number;
	price: number;
}

export interface OfferResponse {
	id: number;
	title: string;
	description: string;
	coverPhotoPath: string;
	type: OfferType;
	isActive: boolean;
	createdOn: Date;
	updatedOn: Date;
	createdById: number;
	updatedById: number;
	price: number;
}

export interface UpdateOfferRequest {
	title?: string;
	description?: string;
	coverPhotoPath?: string;
	type?: OfferType;
	isActive?: boolean;
	updatedById: number;
	price?: number;
}
