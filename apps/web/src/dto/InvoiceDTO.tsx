export enum InvoiceStatus {
	PENDING = "PENDING",
	PAID = "PAID",
	FAILED = "FAILED",
}

export interface CreateInvoiceRequest {
	amount: number;
	status: InvoiceStatus;
}

export interface InvoiceResponse {
	id: number;
	amount: number;
	status: InvoiceStatus;
	createdOn: Date;
	updatedOn: Date;
}

export interface UpdateInvoiceRequest {
	amount?: number;
	status?: InvoiceStatus;
}
