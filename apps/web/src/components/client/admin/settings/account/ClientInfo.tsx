/* eslint-disable @next/next/no-img-element */
import { BubbleButton } from "@components/ui/BubbleButton";
import { ClientResponse } from "@/dto/ClientDTO";
import { Button } from "@material-tailwind/react";
import { getPhotoPath } from "@utils/photoPaths";

interface ClientInfoProps {
	client: ClientResponse;
	onEdit: () => void;
	onLogout: () => void;
	onDeleteAccount: () => void;
}

export const ClientInfo = ({
	client,
	onEdit,
	onLogout,
	onDeleteAccount,
}: ClientInfoProps) => {
	return (
		<div className=" mx-auto">
			<div className="flex justify-between items-center mb-6">
				<h3 className="text-xl font-semibold">Profile Information</h3>
				<div className="flex items-center space-x-4">
					<Button color="secondary" onClick={onDeleteAccount}>
						Delete Account
					</Button>
					<Button color="secondary" onClick={onLogout}>
						Log Out
					</Button>
					<BubbleButton onClick={onEdit}>Edit Profile</BubbleButton>
				</div>
			</div>

			<div className="space-y-6 bg-white p-6 rounded-lg shadow">
				<div className="flex items-center space-x-4">
					{client.profilePhotoPath && (
						<img
							src={getPhotoPath(client.profilePhotoPath)}
							alt={client.username}
							className="h-20 w-20 rounded-full object-cover"
						/>
					)}
					<div>
						<h4 className="text-lg font-medium">
							{client.username}
						</h4>
						<p className="text-gray-600">{client.email}</p>
					</div>
				</div>

				<div className="grid grid-cols-2 gap-4">
					<div>
						<h5 className="font-medium mb-1">Instagram</h5>
						<p className="text-gray-600">
							{client.instagramHandle || "Not connected"}
						</p>
					</div>
					<div>
						<h5 className="font-medium mb-1">Account Status</h5>
						<p
							className={`${
								client.isActive
									? "text-green-600"
									: "text-red-600"
							}`}
						>
							{client.isActive ? "Active" : "Inactive"}
						</p>
					</div>
				</div>

				<div>
					<h5 className="font-medium mb-2">Billing Details</h5>
					<div className="grid grid-cols-2 gap-4">
						<div>
							<p className="text-sm text-gray-600">Name</p>
							<p>{`${client.billingDetails.firstName} ${client.billingDetails.lastName}`}</p>
						</div>
						<div>
							<p className="text-sm text-gray-600">Country</p>
							<p>{client.billingDetails.country}</p>
						</div>
						<div>
							<p className="text-sm text-gray-600">Address</p>
							<p>{client.billingDetails.streetAddress}</p>
						</div>
						<div>
							<p className="text-sm text-gray-600">City</p>
							<p>{`${client.billingDetails.city}, ${client.billingDetails.state} ${client.billingDetails.zipCode}`}</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
};
