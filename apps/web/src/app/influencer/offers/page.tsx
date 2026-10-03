// src/app/influencer/offers/page.tsx
"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { useOffers, useOffersByInfluencerId } from "@/hooks/useOffers";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { useAuth } from "@/hooks/useAuth";
import { Modal } from "@components/ui/Modal";
import { AddOfferForm } from "@components/influencer/admin/offer/AddOfferForm";
import LikeMeTable from "@components/ui/LikeMeTable";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { CreateOfferRequest } from "@/dto/OfferDTO";
import BubbleButton from "@components/ui/BubbleButton";
import { IoMdAdd } from "react-icons/io";

export default function InfluencerOffersPage() {
	const [isModalOpen, setIsModalOpen] = useState(false);
	const { create, isCreating } = useOffers();
	const { upload, isUploading } = usePublicFileUpload();
	const { getUserId } = useAuth();
	const router = useRouter();

	const influencerId = getUserId();
	const { data: influencerOffers = [], isLoading } = useOffersByInfluencerId(
		influencerId!
	);

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "offers" });
		return result.fileName;
	};

	const handleCreateOffer = async (data: CreateOfferRequest) => {
		await create(data);
		setIsModalOpen(false);
	};

	if (isLoading) {
		return <CustomSkeleton count={3} />;
	}

	const formattedOffers = influencerOffers.map((offer) => ({
		id: offer.id,
		title: offer.title,
		type: offer.type,
		price: `$${offer.price}`,
		status: offer.isActive ? "Active" : "Inactive",
		"Created On": new Date(offer.createdOn).toLocaleDateString(),
	}));

	return (
		<div className="p-6">
			<div className="flex justify-between items-center mb-6">
				<h1 className="text-2xl font-bold">My Offers</h1>
				<BubbleButton onClick={() => setIsModalOpen(true)}>
					<IoMdAdd />
					Add Offer
				</BubbleButton>
			</div>

			<LikeMeTable
				data={formattedOffers}
				onRowClick={(id) => router.push(`/influencer/offers/${id}`)}
				filterableColumns={["type", "status"]}
			/>

			<Modal
				isOpen={isModalOpen}
				setIsOpen={setIsModalOpen}
				title="Add New Offer"
			>
				<AddOfferForm
					onSubmit={handleCreateOffer}
					onClose={() => setIsModalOpen(false)}
					isLoading={isCreating}
					handleUpload={handleUpload}
					isUploading={isUploading}
					influencerId={influencerId!}
				/>
			</Modal>
		</div>
	);
}
