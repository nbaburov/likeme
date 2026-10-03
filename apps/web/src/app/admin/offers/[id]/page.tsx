"use client";
import { useParams, useRouter } from "next/navigation";
import { useOfferById, useOffers } from "@/hooks/useOffers";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { OfferInfo } from "@components/admin/offer/OfferInfo";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { UpdateOfferRequest } from "@/dto/OfferDTO";

export default function OfferDetailPage() {
	const { id } = useParams();
	const router = useRouter();
	const { data: offer, isLoading } = useOfferById(Number(id));
	const { update, delete: deleteOffer, isUpdating, isDeleting } = useOffers();
	const { upload, isUploading } = usePublicFileUpload();

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "offers" });
		return result.fileName;
	};

	const handleDelete = async () => {
		await deleteOffer(Number(id));
		router.push("/admin/offers");
	};

	const handleUpdate = async (data: UpdateOfferRequest) => {
		await update({ id: Number(id), offer: data });
	};

	if (isLoading || !offer) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="p-6">
			<OfferInfo
				offer={offer}
				onDelete={handleDelete}
				onUpdate={handleUpdate}
				onBack={() => router.push("/admin/offers")}
				isUpdating={isUpdating}
				isDeleting={isDeleting}
				handleUpload={handleUpload}
				isUploading={isUploading}
			/>
		</div>
	);
}
