"use client";
import { useState } from "react";
import { Modal } from "@components/ui/Modal";
import { TabNavigation } from "@components/layout/TabNavigation";
import { useInfluencer } from "@hooks/useInfluencer";
import { useInfluencerApplications } from "@hooks/useInfluencerApplications";
import { usePublicFileUpload } from "@/hooks/useFileUpload";
import LikeMeTable from "@components/ui/LikeMeTable";
import { useRouter } from "next/navigation";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";
import BubbleButton from "@components/ui/BubbleButton";
import { IoMdAdd } from "react-icons/io";
import { CreateInfluencerRequest } from "@/dto/InfluencerDTO";
import { AddInfluencerForm } from "@components/admin/influencer/AddInfluencerForm";

const tabs = [
	{ id: "influencers", label: "Influencers" },
	{ id: "applications", label: "Applications" },
];

export default function AdminInfluencersPage() {
	const [activeTab, setActiveTab] = useState("influencers");
	const [isModalOpen, setIsModalOpen] = useState(false);
	const router = useRouter();

	const {
		data: influencers,
		isLoading: isInfluencersLoading,
		create,
		isCreating,
	} = useInfluencer();

	const {
		data: applications,
		isLoading: isApplicationsLoading,
	} = useInfluencerApplications();

	const { upload, isUploading } = usePublicFileUpload();

	const handleUpload = async (file: File) => {
		const result = await upload({ file, prefix: "influencers" });
		return result.fileName;
	};

	const handleCreateInfluencer = async (data: CreateInfluencerRequest) => {
		await create(data);
		setIsModalOpen(false);
	};

	if (isInfluencersLoading || isApplicationsLoading) {
		return <CustomSkeleton count={3} />;
	}


	const formattedInfluencers = influencers.map((inf) => ({
		id: inf.id,
		fullName: `${inf.application.billingDetails.firstName} ${inf.application.billingDetails.lastName}`,
		email: inf.application.email,
		country: inf.application.billingDetails.country,
		instagram: inf.application.instagramHandle,
		status: inf.status.charAt(0).toUpperCase() + inf.status.slice(1).toLowerCase(),
		"Instagram Connected": inf.isInstagramConnected ? "Yes" : "No",
	}));

	const formattedApplications = applications.map((app) => ({
		id: app.id,
		fullName: `${app.billingDetails.firstName} ${app.billingDetails.lastName}`,
		email: app.email,
		country: app.billingDetails.country,
		instagram: app.instagramHandle,
		status: app.isApproved ? "Approved" : "Pending",
		submittedOn: new Date(app.createdOn).toLocaleDateString(),
	}));

	return (
		<div className="p-6">
			<div className="flex justify-between items-center mb-6">
				<h1 className="text-2xl font-bold">Influencer Management</h1>
				{activeTab === "influencers" && (
					<BubbleButton onClick={() => setIsModalOpen(true)}>
						<IoMdAdd />
						Add Influencer
					</BubbleButton>
				)}
			</div>

			<TabNavigation
				tabs={tabs}
				activeTab={activeTab}
				onTabChange={setActiveTab}
			/>

			<div className="mt-6 p-6 bg-slate-50 rounded-lg">
				<LikeMeTable
					data={
						activeTab === "influencers"
							? formattedInfluencers
							: formattedApplications
					}
					onRowClick={(id) =>
						router.push(
							activeTab === "influencers"
								? `/admin/influencers/${id}`
								: `/admin/influencers/applications/${id}`
						)
					}
					filterableColumns={["status", "Instagram Connected", "country"]}
				/>
			</div>

			<Modal
				isOpen={isModalOpen}
				setIsOpen={setIsModalOpen}
				title="Add New Influencer"
			>
				<AddInfluencerForm
					onSubmit={handleCreateInfluencer}
					onClose={() => setIsModalOpen(false)}
					isLoading={isCreating}
					handleUpload={handleUpload}
					isUploading={isUploading}
				/>
			</Modal>
		</div>
	);
}
