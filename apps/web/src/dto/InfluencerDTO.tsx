import { CreateInfluencerApplicationRequest, InfluencerApplicationResponse, UpdateInfluencerApplicationRequest } from './InfluencerApplicationDTO';

export type InfluencerStatus = 'PENDING_SETUP' | 'PENDING_INSTAGRAM' | 'ACTIVE' | 'INACTIVE';

export interface InfluencerResponse {
    id: number;
    application: InfluencerApplicationResponse;
    isInstagramConnected: boolean;
    instagramAccessToken: string;
    status: InfluencerStatus;
    isActive: boolean;
    createdOn: Date;
    updatedOn: Date;
    lastLoginOn: Date;
}

export interface CreateInfluencerRequest {
    application: CreateInfluencerApplicationRequest;
    password: string;
    isInstagramConnected?: boolean;
    instagramAccessToken?: string;
    status?: InfluencerStatus;
}

export interface UpdateInfluencerRequest {
    application?: UpdateInfluencerApplicationRequest;
    password?: string;
    isInstagramConnected?: boolean;
    instagramAccessToken?: string;
    status?: InfluencerStatus;
    isActive?: boolean;
}

export interface ConnectInstagramRequest {
    instagramAccessToken: string;
}

export interface CompleteSetupRequest {
    password: string;
    token: string;
}