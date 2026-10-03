/* eslint-disable @next/next/no-img-element */
import {
    Card,
    CardBody,
    Typography,
  } from "@material-tailwind/react";
import { useRouter } from "next/navigation";

interface InfluencerCardPropsType {
    profilePhoto: string;
    coverPhoto: string;
    name: string;
    description: string;
    instagramHandle: string;
    country: string;
}

export function InfluencerCard({
    profilePhoto,
    coverPhoto,
    name,
    description,
    instagramHandle,
    country,
    id,
}: InfluencerCardPropsType & { id: number }) {
    const router = useRouter();

    return (
        <Card 
            className="border border-gray-300 cursor-pointer transition-transform hover:scale-105"
            onClick={() => router.push(`/influencers/${id}`)}
        >
            <CardBody className="pb-6">
                <div className="relative">
                    <img src={coverPhoto} alt={`${name}'s cover`} className="w-full h-48 object-cover" />
                    <img 
                        src={profilePhoto} 
                        alt={`${name}'s profile`} 
                        className="absolute -bottom-6 left-6 w-16 h-16 rounded-full border-4 border-white object-cover"
                    />
                </div>
                <div className="mt-8">
                    <Typography variant="h5" className="mb-2">{name}</Typography>
                    <Typography className="text-gray-600 mb-2 text-sm">{country}</Typography>
                    <Typography className="text-gray-700 mb-3">{description}</Typography>
                    <Typography className="text-blue-500">@{instagramHandle}</Typography>
                </div>
            </CardBody>
        </Card>
    );
}

export default InfluencerCard;
