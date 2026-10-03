/* eslint-disable @typescript-eslint/no-explicit-any */
export type PlatformSettingType = 'COMMISSION_PERCENTAGE' | 'MIN_PASSWORD_LENGTH' | 'MAX_PASSWORD_LENGTH'; // Add all your enum values

export interface PlatformSettingsResponse {
    id: number;
	type: PlatformSettingType;
	value: any;
	updatedOn: Date;
	updatedBy: string;
}

export interface SettingValue {
    type: PlatformSettingType;
    value: string;
}

export interface UpdateSettingsRequest {
    settings: SettingValue[];
}