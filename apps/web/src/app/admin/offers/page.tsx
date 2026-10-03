/* eslint-disable react-hooks/rules-of-hooks */
"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { useOffers } from "@/hooks/useOffers";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { Modal } from "@components/ui/Modal";
import { AddOfferForm } from "@components/admin/offer/AddOfferForm";
import LikeMeTable from "@components/ui/LikeMeTable";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { CreateOfferRequest } from "@/dto/OfferDTO";
import BubbleButton from "@components/ui/BubbleButton";
import { IoMdAdd } from "react-icons/io";
import { useInfluencer } from "@/hooks/useInfluencer";

export default function OffersPage() {
	const [isModalOpen, setIsModalOpen] = useState(false);
	const { data: offers, isLoading, create, isCreating } = useOffers();
	const { data: influencers, isLoading: isInfluencersLoading } = useInfluencer();
	const { upload, isUploading } = usePublicFileUpload();
	const router = useRouter();

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "offers" });
		return result.fileName;
	};

	const handleCreateOffer = async (data: CreateOfferRequest) => {
		await create(data);
		setIsModalOpen(false);
	};

	if (isLoading || isInfluencersLoading) {
		return <CustomSkeleton count={3} />;
	}

	const formattedOffers = offers.map((offer) => {

		return {
			id: offer.id,
			title: offer.title,
			type: offer.type,
			"Created By": offer.createdById ? influencers?.find(influencer => influencer.id === offer.createdById)?.application.instagramHandle : "Unknown",
			price: `$${offer.price}`,
			active: offer.isActive ? "Yes" : "No",
			"Created On": new Date(offer.createdOn).toLocaleDateString(),
		};
	});

	return (
		<div className="p-6">
			<div className="flex justify-between items-center mb-6">
				<h1 className="text-2xl font-bold">Offers</h1>
				<BubbleButton onClick={() => setIsModalOpen(true)}>
					<IoMdAdd />
					Add Offer
				</BubbleButton>
			</div>

			<LikeMeTable
				data={formattedOffers}
				onRowClick={(id) => router.push(`/admin/offers/${id}`)}
				filterableColumns={["type", "active", "Created By"]}
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
				/>
			</Modal>
		</div>
	);
}
