import { OrderDetailsRequest } from "@/dto/OrderDTO";
import { OfferType } from "@/dto/OfferDTO";
import { Button } from "@material-tailwind/react";
import { Input } from "@/components/ui/Input";

interface OrderFormProps {
	offerId: number;
	offerType: OfferType;
	onSubmit: (details: OrderDetailsRequest) => Promise<void>;
	isLoading: boolean;
	details: OrderDetailsRequest;
	onDetailsChange: (details: OrderDetailsRequest) => void;
}

export function OrderForm({ 
	isLoading, 
	details, 
	onDetailsChange,
	onSubmit,
	offerType 
}: OrderFormProps) {
	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		await onSubmit(details);
	};

	const showPostId = offerType === OfferType.LIKE || offerType === OfferType.COMMENT;
	const showComment = offerType === OfferType.COMMENT;

	return (
		<form onSubmit={handleSubmit} className="space-y-4">
			{showPostId && (
				<Input
					label="Instagram Post URL or ID"
					name="postId"
					value={details.postId}
					onChange={(e) => 
						onDetailsChange({
							...details,
							postId: e.target.value
						})
					}
					placeholder="Enter the Instagram post URL or ID"
					required
				/>
			)}

			{showComment && (
				<Input
					label="Comment"
					name="comment"
					type="textarea"
					value={details.comment}
					onChange={(e) => 
						onDetailsChange({
							...details,
							comment: e.target.value
						})
					}
					placeholder="Enter your desired comment"
					required
					rows={3}
				/>
			)}

			<Button type="submit" disabled={isLoading} className="w-full">
				{isLoading ? "Processing..." : "Place Order"}
			</Button>
		</form>
	);
}
