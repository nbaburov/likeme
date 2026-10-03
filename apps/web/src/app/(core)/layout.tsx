import ClientNavBar from "@components/layout/client/ClientNavBar";
import Footer from "@components/client/client/Footer";

export default function CoreLayout({
	children,
}: {
	children: React.ReactNode;
}) {
	return (
		<>
			<ClientNavBar />
			{children}
			<Footer />
		</>
	);
}
