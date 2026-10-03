/* eslint-disable @next/next/no-img-element */
import { OfferResponse } from "@/dto/OfferDTO";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { Card, CardBody, Typography } from "@material-tailwind/react";
import { getPhotoPath } from "@utils/photoPaths";
import { useRouter } from "next/navigation";

interface InfluencerOfferGridProps {
	offers: OfferResponse[];
	isLoading: boolean;
}

export function InfluencerOfferGrid({
	offers,
	isLoading,
}: InfluencerOfferGridProps) {
	const router = useRouter();

	if (isLoading) {
		return <CustomSkeleton />;
	}

	if (!offers.length) {
		return (
			<Typography className="text-gray-600">
				No offers available at the moment.
			</Typography>
		);
	}

	return (
		<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
			{offers.map((offer) => (
				<Card
					key={offer.id}
					className="border border-gray-300 cursor-pointer transition-transform hover:scale-105"
					onClick={() => router.push(`/offers/${offer.id}`)}
				>
					<CardBody>
						<img
							src={getPhotoPath(offer.coverPhotoPath)}
							alt={offer.title}
							className="w-full h-48 object-cover mb-4 rounded"
						/>
						<Typography variant="h5" className="mb-2">
							{offer.title}
						</Typography>
						<Typography className="text-gray-600 mb-2">
							{offer.description}
						</Typography>
						<Typography className="text-likeme-primary font-bold">
							${offer.price}
						</Typography>
						<Typography className="text-gray-500 text-sm mt-2">
							Type: {offer.type}
						</Typography>
					</CardBody>
				</Card>
			))}
		</div>
	);
}
