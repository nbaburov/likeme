import InfoPageLayout from "@components/layout/client/InfoPageLayout";
import { CustomSkeleton } from "@components/ui/CustomSkeleton";

export default function Terms() {
	return (
		<InfoPageLayout title="Terms of Service">
			<CustomSkeleton count={10} />
		</InfoPageLayout>
	);
}
