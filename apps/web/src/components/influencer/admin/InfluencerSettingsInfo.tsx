/* eslint-disable @next/next/no-img-element */
import { BubbleButton } from "@components/ui/BubbleButton";
import { InfluencerResponse } from "@/dto/InfluencerDTO";
import { Button } from "@material-tailwind/react";
import { getPhotoPath } from "@utils/photoPaths";

interface InfluencerSettingsInfoProps {
	influencer: InfluencerResponse;
	onEdit: () => void;
	onLogout: () => void;
	onDeleteAccount: () => void;
	isDeleting: boolean;
}

export const InfluencerSettingsInfo = ({
	influencer,
	onEdit,
	onLogout,
	onDeleteAccount,
	isDeleting,
}: InfluencerSettingsInfoProps) => {
	return (
		<div className="mx-auto">
			<div className="flex justify-between items-center mb-6">
				<h3 className="text-xl font-semibold">Profile Information</h3>
				<div className="flex items-center space-x-4">
					<Button
						color="error"
						onClick={onDeleteAccount}
						disabled={isDeleting}
					>
						{isDeleting ? "Deleting..." : "Delete Account"}
					</Button>
					<Button color="secondary" onClick={onLogout}>
						Log Out
					</Button>
					<BubbleButton onClick={onEdit}>Edit Profile</BubbleButton>
				</div>
			</div>

			<div className="space-y-6 bg-white p-6 rounded-lg shadow">
				<div className="flex items-center space-x-4">
					{influencer.application.profilePhotoPath && (
						<img
							src={getPhotoPath(
								influencer.application.profilePhotoPath
							)}
							alt={influencer.application.username}
							className="h-20 w-20 rounded-full object-cover"
						/>
					)}
					<div>
						<h4 className="text-lg font-medium">
							{influencer.application.username}
						</h4>
						<p className="text-gray-600">
							{influencer.application.email}
						</p>
					</div>
				</div>

				<div className="grid grid-cols-2 gap-4">
					<div>
						<h5 className="font-medium mb-1">Instagram</h5>
						<p className="text-gray-600">
							{influencer.application.instagramHandle ||
								"Not connected"}
						</p>
					</div>
					<div>
						<h5 className="font-medium mb-1">Account Status</h5>
						<p
							className={`${
								influencer.isActive
									? "text-green-600"
									: "text-red-600"
							}`}
						>
							{influencer.isActive ? "Active" : "Inactive"}
						</p>
					</div>
				</div>

				<div>
					<h5 className="font-medium mb-2">Billing Details</h5>
					<div className="grid grid-cols-2 gap-4">
						<div>
							<p className="text-sm text-gray-600">Name</p>
							<p>{`${influencer.application.billingDetails.firstName} ${influencer.application.billingDetails.lastName}`}</p>
						</div>
						<div>
							<p className="text-sm text-gray-600">Country</p>
							<p>
								{influencer.application.billingDetails.country}
							</p>
						</div>
						<div>
							<p className="text-sm text-gray-600">Address</p>
							<p>
								{
									influencer.application.billingDetails
										.streetAddress
								}
							</p>
						</div>
						<div>
							<p className="text-sm text-gray-600">City</p>
							<p>{`${influencer.application.billingDetails.city}, ${influencer.application.billingDetails.state} ${influencer.application.billingDetails.zipCode}`}</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
};
