"use client";
import { useParams, useRouter } from "next/navigation";
import { useInfluencerById, useInfluencer } from "@/hooks/useInfluencer";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import { InfluencerInfo } from "@components/admin/influencer/InfluencerInfo";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { UpdateInfluencerRequest } from "@/dto/InfluencerDTO";

export default function InfluencerDetailPage() {
	const { id } = useParams();
	const router = useRouter();

	// Queries
	const {
		data: influencer,
		isLoading,
	} = useInfluencerById(Number(id));

	// Mutations
	const {
		update,
		delete: deleteInfluencer,
		isUpdating,
		isDeleting,
	} = useInfluencer();

	// File Upload
	const { upload, isUploading } = usePublicFileUpload();

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "influencers" });
		return result.fileName;
	};

	const handleDelete = async () => {
		await deleteInfluencer(Number(id));
		router.push("/admin/influencers");
	};

	const handleUpdate = async (data: UpdateInfluencerRequest) => {
		await update({ id: Number(id), influencer: data });
	};

	if (isLoading || !influencer) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="p-6">
			<InfluencerInfo
				influencer={influencer}
				onDelete={handleDelete}
				onUpdate={handleUpdate}
				onBack={() => router.push("/admin/influencers")}
				isUpdating={isUpdating}
				isDeleting={isDeleting}
				handleUpload={handleUpload}
				isUploading={isUploading}
			/>
		</div>
	);
}
