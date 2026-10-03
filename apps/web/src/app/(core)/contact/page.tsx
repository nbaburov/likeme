"use client";
import InfoPageLayout from "@components/layout/client/InfoPageLayout";
import { ContactGrid } from "@components/client/client/ContactGrid";

function FAQPage() {
	return (
		<InfoPageLayout title="Contact Us">
			<div>
				<ContactGrid />
			</div>
		</InfoPageLayout>
	);
}

export default FAQPage;
