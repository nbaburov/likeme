import { ImageTrailHero } from "@components/client/client/Hero";
import Features from "@components/client/client/Features";
import ClientFAQ from "@components/client/client/Faq";

export default function Home() {
	return (
		<div>
			<main>
				<ImageTrailHero />
				<Features />
				<ClientFAQ />
			</main>
		</div>
	);
}
