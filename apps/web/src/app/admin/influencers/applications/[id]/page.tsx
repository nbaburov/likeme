"use client";
import { useParams, useRouter } from "next/navigation";
import {
	useInfluencerApplicationById,
	useInfluencerApplications,
} from "@hooks/useInfluencerApplications";
import { ApplicationInfo } from "@components/admin/influencer/applications/ApplicationInfo";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import { UpdateInfluencerApplicationRequest } from "@/dto/InfluencerApplicationDTO";

export default function ApplicationDetailPage() {
	const { id } = useParams();
	const router = useRouter();

	// Queries
	const {
		data: application,
		isLoading,
	} = useInfluencerApplicationById(Number(id));

	// Mutations
	const { update, isUpdating } = useInfluencerApplications();

	const handleUpdate = async (data: UpdateInfluencerApplicationRequest) => {
		await update({
			id: Number(id),
			application: {
				...data,
				isApproved: true,
			},
		});
		router.push("/admin/influencers");
	};

	if (isLoading || !application) {
		return <CustomSkeleton count={3} />;
	}

	return (
		<div className="p-6">
			<ApplicationInfo
				application={application}
				onApprove={handleUpdate}
				onBack={() => router.push("/admin/influencers")}
				isApproving={isUpdating}
			/>
		</div>
	);
}
