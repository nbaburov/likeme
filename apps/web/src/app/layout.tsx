/* eslint-disable @next/next/no-sync-scripts */
"use client";
import "../styles/globals.css";
import ReactQueryProvider from "@utils/ReactQueryProvider";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Head from "next/head";

export default function RootLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	return (
		<html lang="en">
			<Head>
				<title>LikeMe</title>
				<meta
					name="description"
					content="Buy exposure, social media likes, followers, comments from celebrities."
				/>
				<link rel="icon" href="/favicon.ico" />
			</Head>
			<body className="antialiased bg-likeme-third">
				<ReactQueryProvider>
					<main>
						<ToastContainer />
						{children}
					</main>
				</ReactQueryProvider>
			</body>
		</html>
	);
}
