/* eslint-disable @next/next/no-img-element */
"use client";

import { useInfluencerById } from "@/hooks/useInfluencer";
import { useOffersByInfluencerId } from "@/hooks/useOffers";
import { useParams } from "next/navigation";
import { InfluencerOfferGrid } from "@/components/client/client/InfluencerOfferGrid";
import InfoPageLayout from "@/components/layout/client/InfoPageLayout";
import { CustomSkeleton } from "@/components/ui/CustomSkeleton";
import { Alert } from "@material-tailwind/react";
import { getPhotoPath } from "@utils/photoPaths";

export default function InfluencerProfilePage() {
	const params = useParams();
	const id = Number(params.id);

	const { data: influencer, isLoading: influencerLoading } =
		useInfluencerById(id);

	const { data: influencerOffers = [], isLoading: offersLoading } =
		useOffersByInfluencerId(id);

	if (influencerLoading || offersLoading) {
		return (
			<InfoPageLayout title={`Loading...`}>
				<CustomSkeleton />
			</InfoPageLayout>
		);
	}

	if (!influencer) {
		return (
			<InfoPageLayout title={`Influencer not found`}>
				<Alert color="error" className="mt-4">
					Influencer not found
				</Alert>
			</InfoPageLayout>
		);
	}

	return (
		<InfoPageLayout title={`${influencer.application.username}'s Profile`}>
			<div className="max-w-7xl mx-auto px-4 py-8">
				{/* Profile Header */}
				<div className="relative mb-8">
					<img
						src={getPhotoPath(
							influencer.application.coverPhotoPath
						)}
						alt="Cover"
						className="w-full h-64 object-cover rounded-lg"
					/>
					<div className="absolute -bottom-16 left-8">
						<img
							src={getPhotoPath(
								influencer.application.profilePhotoPath
							)}
							alt="Profile"
							className="w-32 h-32 rounded-full border-4 border-white"
						/>
					</div>
				</div>

				{/* Profile Info */}
				<div className="mt-20 mb-12">
					<h1 className="text-3xl font-bold mb-2">
						{influencer.application.username}
					</h1>
					<p className="text-gray-600 mb-4">
						{influencer.application.about}
					</p>
					<div className="flex gap-4 text-sm text-gray-500">
						<span>@{influencer.application.instagramHandle}</span>
						<span>•</span>
						<span>
							{influencer.application.billingDetails.country}
						</span>
					</div>
				</div>

				{/* Offers Section */}
				<div>
					<h2 className="text-2xl font-bold mb-6">
						Available Offers
					</h2>
					<InfluencerOfferGrid
						offers={influencerOffers}
						isLoading={offersLoading}
					/>
				</div>
			</div>
		</InfoPageLayout>
	);
}
