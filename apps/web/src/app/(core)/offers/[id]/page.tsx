/* eslint-disable @next/next/no-img-element */
/* eslint-disable @typescript-eslint/no-unused-vars */
"use client";

import { useOfferById } from "@/hooks/useOffers";
import { useOrders } from "@/hooks/useOrders";
import { useParams, useRouter } from "next/navigation";
import InfoPageLayout from "@/components/layout/client/InfoPageLayout";
import { OrderForm } from "@/components/client/client/OrderForm";
import {
	Button,
	Card,
	CardBody,
	Typography,
	Alert,
} from "@material-tailwind/react";
import { OfferType } from "@/dto/OfferDTO";
import { useState } from "react";
import { CustomSkeleton } from "@/components/ui/CustomSkeleton";
import { OrderDetailsRequest } from "@/dto/OrderDTO";
import { getPhotoPath } from "@utils/photoPaths";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "react-toastify";

export default function OfferDetailsPage() {
	const params = useParams();
	const router = useRouter();
	const id = Number(params.id);
	const [showOrderForm, setShowOrderForm] = useState(false);
	const [orderDetails, setOrderDetails] = useState<OrderDetailsRequest>({
		postId: "",
		comment: "",
	});

	const { data: offer, isLoading, error } = useOfferById(id);
	const { create, isCreating, createError } = useOrders();
	const { getUserRole, isAuthenticated } = useAuth();
	const userRole = getUserRole();
	const isClient = userRole === "CLIENT";
	const isAuthed = isAuthenticated();

	const handleOrderSubmit = async (details: OrderDetailsRequest) => {
		try {
			await create({
				offerId: id,
				details,
			});
			router.push("/account/invoices");
		} catch (error) {
			// Error handling is managed by the hook
		}
	};

	const handleOrderClick = () => {
		if (!isAuthed) {
			toast.error("Please sign in to place an order");
			router.push("/sign-in");
			return;
		}

		if (!isClient) {
			toast.error("Only clients can place orders");
			router.push("/sign-in");
			return;
		}

		setShowOrderForm(true);
	};

	if (isLoading) {
		return <CustomSkeleton />;
	}

	if (error) {
		return (
			<InfoPageLayout title="Error">
				<Alert color="error" className="mx-auto max-w-4xl mt-8">
					{error.message}
				</Alert>
			</InfoPageLayout>
		);
	}

	if (!offer) {
		return (
			<InfoPageLayout title="Offer not found">
				<Alert color="warning" className="mx-auto max-w-4xl mt-8">
					Offer not found
				</Alert>
			</InfoPageLayout>
		);
	}

	const getOfferTypeDescription = (type: OfferType) => {
		switch (type) {
			case OfferType.LIKE:
				return "Get likes on your Instagram post";
			case OfferType.COMMENT:
				return "Get comments on your Instagram post";
			case OfferType.FOLLOW:
				return "Get followed by this influencer";
			default:
				return "";
		}
	};

	return (
		<InfoPageLayout title={offer.title}>
			<div className="max-w-4xl mx-auto px-4 py-8">
				<Card className="mb-8">
					<CardBody>
						<img
							src={getPhotoPath(offer.coverPhotoPath)}
							alt={offer.title}
							className="w-full h-64 object-cover rounded-lg mb-6"
						/>

						<Typography variant="h3" className="mb-4">
							{offer.title}
						</Typography>

						<Typography className="text-gray-700 mb-6">
							{offer.description}
						</Typography>

						<div className="mb-6">
							<Typography variant="h6" className="mb-2">
								Type: {offer.type}
							</Typography>
							<Typography className="text-gray-600">
								{getOfferTypeDescription(offer.type)}
							</Typography>
						</div>

						<Typography
							variant="h4"
							color="primary"
							className="mb-6"
						>
							${offer.price}
						</Typography>

						{!showOrderForm ? (
							<Button
								onClick={handleOrderClick}
								className="w-full"
							>
								Order Now
							</Button>
						) : (
							isClient && (
								<div className="border-t pt-6">
									<Typography variant="h5" className="mb-4">
										Complete Your Order
									</Typography>
									{createError && (
										<Alert color="error" className="mb-4">
											{createError.message}
										</Alert>
									)}
									<OrderForm
										offerId={offer.id}
										offerType={offer.type}
										onSubmit={handleOrderSubmit}
										isLoading={isCreating}
										details={orderDetails}
										onDetailsChange={setOrderDetails}
									/>
								</div>
							)
						)}
					</CardBody>
				</Card>
			</div>
		</InfoPageLayout>
	);
}
