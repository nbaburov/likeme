export interface AdminResponse {
	id: number;
	username: string;
	email: string;
	permissions: AdminPermissions;
	profilePhotoPath: string | null;
	isActive: boolean;
	createdOn: string;
	updatedOn: string;
	lastLoginOn: string | null;
}

export type AdminPermissions = "FULL" | "MODERATOR";

export interface CreateAdminRequest {
	username: string;
	email: string;
	password: string;
	permissions: AdminPermissions;
	profilePhotoPath?: string;
}

export interface UpdateAdminRequest {
	username?: string;
	email?: string;
	password?: string;
	permissions?: AdminPermissions;
	profilePhotoPath?: string;
	isActive?: boolean;
}
