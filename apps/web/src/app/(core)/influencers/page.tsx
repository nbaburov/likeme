"use client";

import { usePublicInfluencer } from "@/hooks/useInfluencer";
import InfoPageLayout from "@/components/layout/client/InfoPageLayout";
import { InfluencerCard } from "@components/client/client/InfluencersCard";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { Select } from "@components/ui/Select";
import { countries } from "@/utils/countries";
import { useState } from "react";
import { Alert, Typography, Card, CardBody } from "@material-tailwind/react";
import { getPhotoPath } from "@utils/photoPaths";

function Influencers() {
	const { data: influencers, isLoading, error } = usePublicInfluencer();

	const [selectedCountry, setSelectedCountry] = useState("ALL");

	const countryOptions = [
		{ value: "ALL", label: "All Countries" },
		...countries,
	];


	const filteredInfluencers =
		selectedCountry === "ALL"
			? influencers
			: influencers.filter(
					(influencer) =>
						influencer.application.billingDetails.country ===
						selectedCountry
			  );

	if (isLoading) {
		return (
			<InfoPageLayout title="Our Influencers">
				<CustomSkeleton count={11} />
			</InfoPageLayout>
		);
	}

	if (error) {
		return (
			<InfoPageLayout title="Our Influencers">
				<Alert color="error" className="mt-4">
					{error.message}
				</Alert>
			</InfoPageLayout>
		);
	}

	return (
		<InfoPageLayout title="Our Influencers">
			<Card className="w-full max-w-screen-xl mx-auto">
				<CardBody>
					<div className="max-w-xs mx-auto mb-6">
						<Select
							label="Filter by Country"
							name="country"
							value={selectedCountry}
							onChange={(e) => setSelectedCountry(e.target.value)}
							options={countryOptions}
						/>
					</div>
					
					<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
						{filteredInfluencers.map((influencer) => (
							<InfluencerCard
								key={influencer.id}
								id={influencer.id}
								profilePhoto={getPhotoPath(
									influencer.application.profilePhotoPath
								)}
								coverPhoto={getPhotoPath(
									influencer.application.coverPhotoPath
								)}
								name={influencer.application.username}
								description={influencer.application.about}
								instagramHandle={influencer.application.instagramHandle}
								country={influencer.application.billingDetails.country}
							/>
						))}
					</div>

					{filteredInfluencers.length === 0 && (
						<Typography
							variant="paragraph"
							color="inherit"
							className="text-center mt-8"
						>
							No influencers found for the selected country
						</Typography>
					)}
				</CardBody>
			</Card>
		</InfoPageLayout>
	);
}

export default Influencers;
