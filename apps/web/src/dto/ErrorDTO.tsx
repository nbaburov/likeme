export interface ErrorResponseDTO {
	code: string;
	message: string;
	details: string | Record<string, string>;
	timestamp: string;
}

export class ApiError extends Error {
	constructor(
		public code: string,
		public title: string,
		public details: string | Record<string, string>,
		public timestamp: string
	) {
		super(title);
		this.name = "ApiError";
	}

	static fromDTO(dto: ErrorResponseDTO): ApiError {
		return new ApiError(dto.code, dto.message, dto.details, dto.timestamp);
	}
}
